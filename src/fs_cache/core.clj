(ns fs-cache.core
  (:require [clojure.java.io :as io]
            [clojure.core.cache :as cache]
            [taoensso.nippy :as nippy]))

(defn- cache-path [{:keys [root-path]} k]
  (io/file root-path k))

(defrecord FileSystemCache [root-path]
  clojure.core.cache/CacheProtocol
  (lookup [this e]
    (nippy/thaw-from-file (cache-path this e)))
  (has? [this e] (.exists (cache-path this e)))
  (hit [this e] this)
  (miss [this e value]
    (doto (cache-path this e)
      io/make-parents
      (nippy/freeze-to-file value))
    this)
  (evict [this e]
    (io/delete-file (cache-path this e))
    this)
  (seed [this base]
    this))
