(ns simulated-genetics.core-test
  (:require [clojure.test :refer :all]
            [cc.journeyman.simulated-genetics.genome :refer :all]))

(deftest clone-test
  (testing "All bits should come from one or other parent. If parent genomes
            are identical, the offspring is a clone."
    (let [g (rand-genome)
          c (create-genome g g)]
      (is (= c g)))))
