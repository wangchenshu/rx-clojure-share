(ns rx-clojure-share.core
  (:require [beicon.core :as rx]))

(defn new-line []
  (println))

(def stream-coll (rx/from-coll [101 202 303]))
(def stream-range (rx/range 3))
(def my-atom (atom 100))
(def stream-atom (rx/from-atom my-atom))
(def stream-just (rx/just 24))
(def stream-of (rx/of 10 9 8))
;(def stream-timeout (rx/timeout 1000 10))

(def stream-factory
  (rx/create (fn [sick]
               (sick 11)          ;; next with `11` as value
               (sick (rx/end 22)) ;; next with `22` as value and end the stream
               (fn []
                 ;; function called on unsubscription
                 ))))

(def my-sub-1 (rx/subscribe stream-just
                            #(println "on-value: " %)
                            #(println "on-error: " %)
                            #(println "on-end")))

(def stream-filter (->> (rx/from-coll [1 2 3 4 5])
                        (rx/filter #(> % 2))))

(def stream-map (->> (rx/from-coll [1 2 3 4 5])
                     (rx/map inc)))

(def stream-flatmap (->> (rx/from-coll [4 5 6])
                         (rx/map #(rx/from-coll (range % (+ % 2))))
                         (rx/flat-map)))

(def stream-skip (->> (rx/from-coll [1 2 3 4 5])
                      (rx/skip 4)))

(def stream-skip-while (->> (rx/from-coll [1 1 1 1 1 2 3])
                            (rx/skip-while odd?)))

(def stream-take (->> (rx/from-coll [9 8 7 6 5])
                      (rx/take 3)))

(def stream-take-while (->> (rx/from-coll [1 1 1 1 1 2 3])
                            (rx/take-while odd?)))

(def stream-reduce (->> (rx/from-coll [1 2 3 4 5 6 7 8 9 10])
                        (rx/reduce + 0)))

(def stream-scan (->> (rx/from-coll [1 2 3 4])
                      (rx/scan + 0)))

(def stream-buffer (->> (rx/from-coll [1 2 3 4 5 6])
                        (rx/buffer 3)))

(def stream-zip (->> (rx/zip
                      (rx/from-coll [1 2 3])
                      (rx/from-coll [4 5 6]))))

(def stream-concat (->> (rx/concat
                         (rx/from-coll [1 2 3])
                         (rx/from-coll [4 5 6]))))

(def stream-merge (->> (rx/merge
                        (rx/from-coll [1 2 3])
                        (rx/from-coll [4 5 6]))))

(def my-bus (rx/bus))
(def stream-bus (->> my-bus
                     (rx/skip 1)
                     (rx/map inc)
                     (rx/take 2)))

(defn -main [& args]
  
  ;; from coll
  (rx/on-value stream-coll #(println "(coll) it: " %))

  (new-line)
  ;; from range
  (rx/on-value stream-range #(println "(range) it: " %))

  (new-line)
  ;; from atom
  (rx/on-value stream-atom #(println "(atom) it: " %))
  (swap! my-atom inc)

  (new-line)
  ;; from value
  (rx/on-value stream-just #(println "(just) it: " %))

  (new-line)
  ;; from multiple values
  (rx/on-value stream-of #(println "(of) it: " %))  

  ;; empty
  ;;(def stream-empty (rx/empty))

  ;; (new-line)
  ;; timeout
  ;;(rx/on-value stream-timeout #(println "(timeout) it: " %))

  (new-line)
  ;; factory
  (rx/on-value stream-factory #(println "(factory) it: " %))
 
  (new-line)
  ;; on-value
  (def my-sub (rx/on-value stream-just #(println "(sub) it: " %)))

  (new-line)
  ;; on-end
  (def my-sub-end (rx/on-end stream-just #(println "(end)")))

  (new-line)
  ;; on-error
  (def my-sub-error (rx/on-error stream-just #(println "(error) it: " %)))

  ;; filter
  (rx/subscribe stream-filter
                #(println "(filter) on-value: " %)
                #(println "(filter) on-error: " %)
                #(println "(filter) on-end"))

  (new-line)
  ;; map
  (rx/subscribe stream-map
                #(println "(map) on-value: " %)
                #(println "(map) on-error: " %)
                #(println "(map) on-end"))

  (new-line)
  ;; flatmap
  (rx/subscribe stream-flatmap
                #(println "(flatmap) on-value: " %)
                #(println "(flatmap) on-error: " %)
                #(println "(flatmap) on-end"))

  (new-line)
  ;; skip
  (rx/subscribe stream-skip
                #(println "(skip) on-value: " %)
                #(println "(skip) on-error: " %)
                #(println "(skip) on-end"))

  (new-line)
  ;; skip-while
  (rx/subscribe stream-skip-while
                #(println "(skip-while) on-value: " %)
                #(println "(skip-while) on-error: " %)
                #(println "(skip-while) on-end"))

  (new-line)
  ;; take
  (rx/subscribe stream-take
                #(println "(take) on-value: " %)
                #(println "(take) on-error: " %)
                #(println "(take) on-end"))
  
  (new-line)
  ;; take-while
  (rx/subscribe stream-take-while
                #(println "(take-while) on-value: " %)
                #(println "(take-while) on-error: " %)
                #(println "(take-while) on-end"))

  (new-line)
  ;; reduce
  (rx/subscribe stream-reduce
                #(println "(reduce) on-value: " %)
                #(println "(reduce) on-error: " %)
                #(println "(reduce) on-end"))

  (new-line)
  ;; scan
  (rx/subscribe stream-scan
                #(println "(scan) on-value: " %)
                #(println "(scan) on-error: " %)
                #(println "(scan) on-end"))

  (new-line)
  ;; buffer
  (rx/subscribe stream-buffer
                #(println "(buffer) on-value: " %)
                #(println "(buffer) on-error: " %)
                #(println "(buffer) on-end"))

  (new-line)
  ;; zip
  (rx/subscribe stream-zip
                #(println "(zip) on-value: " %)
                #(println "(zip) on-error: " %)
                #(println "(zip) on-end"))

  (new-line)
  ;; concat
  (rx/subscribe stream-concat
                #(println "(concat) on-value: " %)
                #(println "(concat) on-error: " %)
                #(println "(concat) on-end"))

  (new-line)
  ;; merge
  (rx/subscribe stream-merge
                #(println "(merge) on-value: " %)
                #(println "(merge) on-error: " %)
                #(println "(merge) on-end"))

  (new-line)
  ;; bus
  (rx/subscribe stream-bus
                #(println "(bus) on-value: " %)
                #(println "(bus) on-error: " %)
                #(println "(bus) on-end"))

  (rx/push! my-bus 1)
  (rx/push! my-bus 2)
  (rx/push! my-bus 1)
  (rx/push! my-bus 2)

  (rx/end! my-bus))
