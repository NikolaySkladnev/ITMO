(require '[clojure.math :as math])
(require '[clojure.string :as string])
(load-file "parser.clj")

(defn operation [function]
  (fn [& args]
    (fn [variables]
      (apply function (map (fn [arg] (arg variables)) args)))))

(defn divide-double [& args] (cond
                               (= (count args) 1) (/ 1 (double (first args)))
                               :else (/ (first args) (double (reduce * (rest args))))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def negate (operation -))
(def sin (operation math/sin))
(def cos (operation math/cos))
(def arithMean (operation (fn [& args] (divide-double (reduce + args) (count args)))))
(def geomMean (operation (fn [& args] (math/pow (abs (reduce * args)) (divide-double (count args))))))
(def harmMean (operation (fn [& args] (divide-double (count args) (reduce + (map (fn [arg] (divide-double arg)) args))))))
(def divide (operation divide-double))

(defn constant [arg] (constantly arg))
(defn variable [arg] (fn [variables] (variables arg)))

(def operations {'+         add,
                 '-         subtract,
                 '*         multiply,
                 '/         divide,
                 'negate    negate,
                 'sin       sin,
                 'cos       cos,
                 'arithMean arithMean,
                 'geomMean  geomMean,
                 'harmMean  harmMean
                 })

(defn createOperation [operation symbol] (fn [& args] {:toString        (str "(" symbol " " (string/join " " (map :toString args)) ")")
                                                       :toStringPostfix (str "(" (string/join " " (map :toStringPostfix args)) " " symbol ")")
                                                       :evaluate        (fn [variables] (apply operation (map (fn [arg] ((arg :evaluate) variables)) args)))}))

(def Add (createOperation + '+))
(def Subtract (createOperation - '-))
(def Multiply (createOperation * '*))
(def Divide (createOperation divide-double '/))
(def Negate (createOperation - 'negate))
(def Ln (createOperation math/log 'ln))
(def Exp (createOperation math/exp 'exp))

(defn Variable [arg1]
  {:toString        arg1
   :toStringPostfix arg1
   :evaluate        (fn [variables] (variables (string/lower-case (first arg1))))})

(defn Constant [arg1]
  {:toString        (str arg1)
   :toStringPostfix (str arg1)
   :evaluate        (constantly arg1)})

(defn evaluate [expression variables] ((expression :evaluate) variables))
(defn toString [expression] (expression :toString))
(defn toStringPostfix [expression] (expression :toStringPostfix))

(def operations-objects {'+      Add
                         '-      Subtract
                         '*      Multiply
                         '/      Divide
                         'negate Negate
                         'exp    Exp,
                         'ln     Ln})

(defn parseExpression [expression const-type var-type operations-type]
  (cond
    (seq? expression) (apply (operations-type (first expression)) (map (fn [arg] (parseExpression arg const-type var-type operations-type)) (rest expression)))
    (symbol? expression) (var-type (str expression))
    (number? expression) (const-type expression)))

(defn parseFunction [expression]
  (parseExpression (read-string expression) constant variable operations))

(defn parseObject [expression]
  (parseExpression (read-string expression) Constant Variable operations-objects))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *number (+str (+plus (+char "0123456789."))))
(defn sign [s tail] (if (#{\- \+} s) (cons s tail) tail))
(def *const (+map (comp Constant read-string) (+str (+seqf sign (+opt (+char "-+")) *number))))

(def *vars (+char "xyzXYZ"))
(def *variable (+map Variable (+str (+plus *vars))))

(def *operation (+map symbol (+seqn 0 (+str (+star (+char-not " \t\n\r()"))))))

(defn operation-parse [[args f]]
  (apply (operations-objects f) args))

(defn *expression [parser]
  (+seqn 1
         (+char "(")
         *ws
         (+map operation-parse (+seqn 0 (+map conj (+seq (+star (+seqn 0 parser *ws)) *operation))))
         *ws
         (+char ")")
         ))

(def *value
  (+or
    *const
    *variable
    (*expression (delay *value))
    ))

(def parseObjectPostfix (+parser (+seqn 0 *ws *value *ws)))
