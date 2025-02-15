; This file should be placed in clojure-solutions
; You may use it via (load-file "parser.clj")

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)


(defn _empty [value] (partial -return value))               ; переопределяет хвост

(defn _char [p]                                             ; проверяет первый символ на p
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]                                              ; берёт результат и принимает к нему функцию f возвращает {f(r), -tail r}
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))

(defn _combine [f a b]                                      ; Парсим {f(a(-value str)), f(b(-tail str))}
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
         ((force b) (-tail ar)))))))

(defn _either [a b]                                         ; Если можем распарсить с помощью a, то парсим, если нет, то парсим с помощью b
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [parser]                                      ; Принимает и применяет парсер
  (fn [input]
    (-value ((_combine (fn [v _] v) parser (_char #{\u0001})) (str input \u0001)))))

(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])



(defn +char [chars] (_char (set chars)))                    ; {/a, /b, /c}
(defn +char-not [chars] (_char (comp not (set chars))))     ; не символы
(defn +map [f parser] (comp (_map f) parser))               ; применяет функцию к результату парсера
(def +ignore (partial +map (constantly 'ignore)))           ; Игнорирует результат парсера

(defn iconj [coll value]                                    ; Игнорирует 'ignore
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& parsers]                                      ; Принимает последовательно парсеры и воззвращает вектора в случае успеха
  (reduce (partial _combine iconj) (_empty []) parsers))

(defn +seqf [f & parsers] (+map (partial apply f) (apply +seq parsers))) ; Применяет функцию к seq

(defn +seqn [n & parsers] (apply +seqf (fn [& vs] (nth vs n)) parsers)) ; В каждом результате берёт n-тый элемент

(defn +or [parser & parsers]                                ; Пытается запарсить разными парсерами
  (reduce (partial _either) parser parsers))

(defn +opt [parser]                                         ; Пытаеться приминить парсер, если не удасться вернёт nil
  (+or parser (_empty nil)))

(defn +star [parser]                                        ; Пока может парсить парсим вернём последовательность
  (letfn [(rec [] (+or (+seqf cons parser (delay (rec))) (_empty ())))] (rec)))

(defn +plus [parser] (+seqf cons parser (+star parser)))    ; Парсим 1 или более раз вернём последовательность

(defn +str [parser] (+map (partial apply str) parser))      ; Делает строчки из векторов

(def +parser _parser)                                       ; Парсер...


(defn +rules [defs]
  (cond
    (empty? defs) ()
    (seq? (first defs)) (let [[[name args body] & tail] defs]
                          (cons
                            {:name name :args args :body body}
                            (+rules tail)))
    :else (let [[name body & tail] defs]
            (cons
              {:name name :args [] :body body :plain true}
              (+rules tail)))))

(defmacro defparser [name & defs]
  (let [rules (+rules defs)
        plain (set (map :name (filter :plain rules)))]
    (letfn [(rule [{name :name, args :args, body :body}] `(~name ~args ~(convert body)))
            (convert [value]
              (cond
                (seq? value) (map convert value)
                (char? value) `(+char ~(str value))
                (contains? plain value) `(~value)
                :else value))]
      `(def ~name (letfn ~(mapv rule rules) (+parser (~(:name (last rules)))))))))
