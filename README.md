# rx-clojure-share

A Clojure library designed to ... well, that part is up to you.

# Rx Clojure
beicon - reactive streams for Clojure(Script)

## Introduction
beicon is a small and concise library that provides reactive streams api for clojure and clojurescript.

### Install
The simplest way to use beicon in a clojure project, is by including it in the dependency vector on your project.clj file:

```clojure
[funcool/beicon "2.2.0"]
```

## Creating Streams
This section will give you the available methods for create observable streams.

### From a collection
The most basic way to create a streamm is just take a collection and convert it in an observable sequence:


```clojure
(ns rx-clojure-share.core
  (:require [beicon.core :as rx]))

(def stream-coll (rx/from-coll [101 202 303]))

(defn -main [& args]
  ;; from coll
  (rx/on-value stream #(println "it: " %)))
```

### From range
An other way to create a observable stream is using the range constructor that is pretty analgous to the clojure’s one:

```clojure
(def stream-range (rx/range 3))
(rx/on-value stream-range #(println "(range) it: " %))
```

### From Atom
Atoms in clojure are watchable, so you can listen its changes.
This method convert that changes in an infinite observable sequence of atom changes:

```clojure
(def my-atom (atom 100))
(def stream-atom (rx/from-atom my-atom))
(rx/on-value stream-atom #(println "(atom) it: " %))
(swap! my-atom inc)
```

### From Value
It creates a observable sequence of one unique value:

```clojure
(def stream-just (rx/just 24))
(rx/on-value stream-just #(println "(just) it: " %))  
```

### From multiple values
It there is a way for create a observable seequence from multiple values, using the of consturctor:

```clojure
(def stream-of (rx/of 10 9 8))
(rx/on-value stream-of #(println "(of) it: " %))    
```

### Empty
Some times you also want just a terminated stream:

```clojure
(def stream-empty (rx/empty))
```
This stream not yelds any value and just terminates.

### With timeout
This allow create an observable seequence of one unique value that will be emited after specified amount of time:

### From factory
This is the most advanced and flexible way to create a observable sequence.
It allows to have control about termination and errors and intended to be used for build other kind of constructors.

```clojure
(def stream-factory
  (rx/create (fn [sick]
               (sick 11)          ;; next with `11` as value
               (sick (rx/end 22)) ;; next with `22` as value and end the stream
               (fn []
                 ;; function called on unsubscription
                 ))))
(rx/on-value stream-factory #(println "(factory) it: " %))
```
This is implemented using protocols for make it flexible and easy extensible by the user. This is how the default impl behaves:

* js/Error or ExceptionInfo instances triggers the error termination of stream.
* (rx/end value) sends the unwrapped value to the stream, then terminate stream.
* rx/end as value triggers the stream termination.
* any other value are valid values for send to the stream.

## Consuming streams

### The stream states
The observable sequence can be in 3 different kind of states: alive, errored or ended.
I an error is emited the stream can be considered ended with an error.
So error or end states can be considered termination states.
And is convenient you can subscribe to any of that states of an observable seequence.

### General purpose
A general purpose subscription is one that allows you create one subscription that watches
all the different possible states of an observable seequence:

```clojure
(def my-sub-1 (rx/subscribe stream-just
                            #(println "on-value: " %)
                            #(println "on-error: " %)
                            #(println "on-end")))
```
The return value of subscribe function is a funcition that can be called for dispose the subscription.

### Consume values
But in most circumstances you only want consume values regardless of any error or termination. For this purposes is there the on-value function:

```clojure
(def my-sub (rx/on-value stream-just #(println "(sub) it: " %)))  
```
Like with subscribe function, on-value function also return a callable that when is called will dispose the created subscription.

### Consume successful termination
With on-end function you can watch the successful termination of an observable sequence:

```clojure
(def my-sub-end (rx/on-end stream-just #(println "(end)")))  
```

### Consume error termination
With on-error function you can watch the error termination of an observable seequence:

```clojure
(def my-sub-error (rx/on-error stream-just #(println "(error) it: " %)))  
```

## Transformations

### Filter
The main advantage of using reactive streams is that you may treat them like normal seequence, and in this case filter them with a predicate:

```clojure
(def stream-filter (->> (rx/from-coll [1 2 3 4 5])
                        (rx/filter #(> % 2))))

(rx/subscribe stream-filter
              #(println "(filter) on-value: " %)
              #(println "(filter) on-error: " %)
              #(println "(filter) on-end"))
```

### Map
Also, you can apply a function over each value in the stream:

```clojure
(def stream-map (->> (rx/from-coll [1 2 3 4 5])
                     (rx/map inc)))

(rx/subscribe stream-map
              #(println "(map) on-value: " %)
              #(println "(map) on-error: " %)
              #(println "(map) on-end"))
```

### Flat Map
Convets a observable seequence that can contain other observable seequences in an other observable seequences that emits just plain values.
The result is similar to concatenate all the underlying seequences.

```clojure
(def stream-flatmap (->> (rx/from-coll [4 5 6])
                         (rx/map #(rx/from-coll (range % (+ % 2))))
                         (rx/flat-map)))

(rx/subscribe stream-flatmap
              #(println "(flatmap) on-value: " %)
              #(println "(flatmap) on-error: " %)
              #(println "(flatmap) on-end"))  
```

### Skip
Also, sometimes you just want to skip values from stream under different criteria.

You can skip the first N values:

```clojure
(def stream-skip (->> (rx/from-coll [1 2 3 4 5])
                      (rx/skip 4)))

(rx/subscribe stream-skip
              #(println "(skip) on-value: " %)
              #(println "(skip) on-error: " %)
              #(println "(skip) on-end"))
```
Skip while some condition evalutates to true:

```clojure
(def stream-skip-while (->> (rx/from-coll [1 1 1 1 1 2 3])
                            (rx/skip-while odd?)))

(rx/subscribe stream-skip-while
              #(println "(skip-while) on-value: " %)
              #(println "(skip-while) on-error: " %)
              #(println "(skip-while) on-end"))  
```
Or skip until an other observable yelds a value using skip-until (no example at this moment).

### Take
You also can limit the observale sequence to an specified number of elements:

```clojure
(def stream-take (->> (rx/from-coll [9 8 7 6 5])
                      (rx/take 3)))

(rx/subscribe stream-take
              #(println "(take) on-value: " %)
              #(println "(take) on-error: " %)
              #(println "(take) on-end"))
```
Or an condition expression evaluates to true:

```clojure
(def stream-take-while (->> (rx/from-coll [1 1 1 1 1 2 3])
                            (rx/take-while odd?)))

(rx/subscribe stream-take-while
              #(println "(take-while) on-value: " %)
              #(println "(take-while) on-error: " %)
              #(println "(take-while) on-end"))  
```

### Reduce
Allows combine all results of an observable seequence using a combining function also called (reducing function):

```clojure
(def stream-reduce (->> (rx/from-coll [1 2 3 4 5 6 7 8 9 10])
                        (rx/reduce + 0)))

(rx/subscribe stream-reduce
              #(println "(reduce) on-value: " %)
              #(println "(reduce) on-error: " %)
              #(println "(reduce) on-end"))  
```

### Scan
Allows combine all results of an observable seequence using a combining function also called (reducing function).
Returns a stream of each intermediate result instead of:

```clojure
(def stream-scan (->> (rx/from-coll [1 2 3 4])
                      (rx/scan + 0)))

(rx/subscribe stream-scan
              #(println "(scan) on-value: " %)
              #(println "(scan) on-error: " %)
              #(println "(scan) on-end"))  
```

### Buffer
This transformer functions allow accomulate N specified values in a buffer and then emits them as one value.

```clojure
(def stream-buffer (->> (rx/from-coll [1 2 3 4 5 6])
                        (rx/buffer 3)))

(rx/subscribe stream-buffer
              #(println "(buffer) on-value: " %)
              #(println "(buffer) on-error: " %)
              #(println "(buffer) on-end"))  
```

## Combinators

### Zip
This combinator combines two observable seequences in one.

```clojure
(def stream-zip (->> (rx/zip
                      (rx/from-coll [1 2 3])
                      (rx/from-coll [4 5 6]))))

(rx/subscribe stream-zip
              #(println "(zip) on-value: " %)
              #(println "(zip) on-error: " %)
              #(println "(zip) on-end"))  
```

### Concat
This cobinator concatenates two or more observable seequences.

```clojure
(def stream-concat (->> (rx/concat
                         (rx/from-coll [1 2 3])
                         (rx/from-coll [4 5 6]))))

(rx/subscribe stream-concat
              #(println "(concat) on-value: " %)
              #(println "(concat) on-error: " %)
              #(println "(concat) on-end"))  
```

### Merge
This combinator merges two or more observable seequences.

```clojure
(def stream-merge (->> (rx/merge
                        (rx/from-coll [1 2 3])
                        (rx/from-coll [4 5 6]))))

(rx/subscribe stream-merge
              #(println "(merge) on-value: " %)
              #(println "(merge) on-error: " %)
              #(println "(merge) on-end"))  
```

## Bus
This is an abstraction that combines observable seequence with the observer.
So you can push values into it and transform and subscribe to it like any other seequence.

### Creating a bus.
You can create a bus instance using bus constructor function.
There is an example of using bus for the both operations: push values and subscribe to it.

```clojure
(def my-bus (rx/bus))
(def stream-bus (->> my-bus
                     (rx/skip 1)
                     (rx/map inc)
                     (rx/take 2)))

(rx/subscribe stream-bus
              #(println "(bus) on-value: " %)
              #(println "(bus) on-error: " %)
              #(println "(bus) on-end"))

(rx/push! my-bus 1)
(rx/push! my-bus 2)
(rx/push! my-bus 1)
(rx/push! my-bus 2)
```

### Ending a bus
You can end bus in any moment just executing end! function:

```clojure
(rx/end! my-bus)
```

## Usage
> 參考 https://funcool.github.io/beicon/latest/ 並加了自已的範例

FIXME

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
