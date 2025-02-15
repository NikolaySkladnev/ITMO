(defn function [f] (fn [v1 v2] (mapv f v1 v2)))

(def v+ (function +))
(def v- (function -))
(def v* (function *))
(def vd (function /))

(defn scalar [v1, v2]
  (reduce + (v* v1 v2))
  )

(defn arg [v1, v2, i, j]
  (- (* (v1 i) (v2 j)) (* (v1 j) (v2 i)))
  )

(defn vect [v1, v2]
  (vector (arg v1 v2 1 2)
          (arg v1 v2 2 0)
          (arg v1 v2 0 1))
  )

(def m+ (function v+))
(def m- (function v-))
(def m* (function v*))
(def md (function vd))

(defn multFunction [f] (fn [a, b] (mapv (fn [aline] (f aline b)) a)))

(defn m*m [a, b]
  (mapv (fn [aline] (mapv (fn [bline] (scalar aline bline)) (apply mapv vector b))) a)
  )

(def v*s (multFunction *))
(def m*s (multFunction v*s))
(def m*v (multFunction scalar))

(defn transpose [a]
  (apply mapv vector a)
  )

(def c+ (function m+))
(def c- (function m-))
(def c* (function m*))
(def cd (function md))
