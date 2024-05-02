(ns cc.journeyman.simulated-genetics.genome
  "lightweight simulation of a genome."
  (:require [cc.journeyman.simulated-genetics.utils :refer [bits-in-genome create-mask long-from-binary-string]]
            [clojure.math :refer [pow]]
            [taoensso.telemere :refer [error! trace!]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; Genome: create, combine, and extract features from a pseudo-genome.
;;;;
;;;; This program is free software; you can redistribute it and/or
;;;; modify it under the terms of the GNU General Public License
;;;; as published by the Free Software Foundation; either version 2
;;;; of the License, or (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU General Public License
;;;; along with this program; if not, write to the Free Software
;;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
;;;; USA.
;;;;
;;;; Copyright (C) 2024 Simon Brooke
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def genome-mask
  "A mask which selects just the bits we're interested in from a long."
  (long (dec (pow 2 bits-in-genome))))

(def gender-bit
  "The bit that encodes for gender"
  25)

(defmacro rand-genome
  "Create a random genome."
  []
  `(long (rand (dec (pow 2 bits-in-genome)))))

(defn create-genome
  "Create a new genome; if `father` and `mother` are passed, the result will 
   comprise bits taken randomly from those genomes."
  ([]
   (create-genome (rand-genome) (rand-genome)))
  ([^Long father ^Long mother]
   (let [mask (rand-genome)]
     (trace! (format "Parents are %s, %s; mask is %s"
                     (Long/toBinaryString father)
                     (Long/toBinaryString mother)
                     (Long/toBinaryString mask)))
     ;; TODO: cycling through a string is inefficient
     (long-from-binary-string
      (apply str
             (map #(if (bit-test (if (bit-test mask %) mother father) %) "1" "0")
                  (reverse (range bits-in-genome))))))))

(defn extract-bits
  "Extract, as an integer left-shifted by `start`, those bits from `g` indexed
   from `start` (inclusive) to `end` (exclusive)."
  [^Long g ^Long start ^Long end]
  (let [mask (trace! (create-mask start end))]
    (long (bit-shift-right (bit-and g mask) (- bits-in-genome end)))))

(defmacro ethnically-biased-feature-index
  "Some feature values are associated with particular ethnicities."
  [genome start end]
  `(+ (extract-bits ~genome ~start ~end)
      (if (bit-test ~genome 3)
        (int (pow (- ~end ~start) 2))
        0)))

(defn male?
  "`true` if this genome is male."
  [genome]
  (bit-test genome gender-bit))

(defn expand-genome
  [^Long genome]
  (let [skin-tone (ethnically-biased-feature-index genome 4 8)]
    {:ethnic-type (extract-bits genome 0 4)
     :skin-tone (ethnically-biased-feature-index genome 4 8)
     :freckles? (= skin-tone 1)
     :hair-colour (nth [:blonde :red :russet :cognac :chestnut
                        :coffee :dark-brown :black]
                       (ethnically-biased-feature-index genome 9 11))
     :eye-colour (nth [:blue :hazel :russet :cognac :chestnut
                       :coffee :dark-brown :black]
                      (ethnically-biased-feature-index genome 12 14))
     :height (+ 150 (* (extract-bits genome 15 18) (if (male? genome) 6 4))) ;; men are taller
     :robustness (extract-bits genome 19 21)
     :aging (extract-bits genome 22 25)
     :gender (if (male? genome) :male :female)
   ;; TODO: face stuff
     }))