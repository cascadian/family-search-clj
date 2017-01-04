(ns family-search.core
  (require [om.next :as om :refer-macros [defui]]
           [family-search.protocols :as fs]))


(defui HrefLink
  static om/IQuery
  (query [this]
    '[:link/href]))

(defui MergeLink
  static om/IQuery
  (query [this]
    '[:link/template
      :link/type
      :link/accept
      :link/allow
      :link/title]))

(defui Link
  static fs/IDatascriptSchema
  (schema [this]
    {:href {}})
  static om/IQuery
  (query [this]
    `{:link/href  (om/get-query HrefLink)
      :link/merge (om/get-query MergeLink)}))

(defui User
  static om/IQueryParams
  (params [this]
    {:link (om/get-query Link)})
  static om/IQuery
  (query [this]
    '[:user/id
      :user/contactName
      :user/helperAccessPin
      :user/givenName
      :user/familyName
      :user/email
      :user/country
      :user/gender
      :user/birthDate
      :user/preferredLanguage
      :user/displayName
      :user/personId
      :user/treeUserId
      {:user/links
       [{:link/person ?link}
        {:link/self ?link}
        {:link/artifacts ?link}]}]))

(defui Gender
  static fs/IDatascriptSchema
  (schema [this]
    {:gender/id          {:db/unique :db.unique/identity}
     :gender/attribution {:db/valueType   :db.type/ref
                          :db/isComponent true}
     :gender/links       {:db/valueType   :db.type/ref
                          :db/cardinality :db.cardinality/one
                          :db/isComponent true}})
  static om/IQuery
  (query [this]
    '[:gender/id
      :gender/attribution
      :gender/type
      :gender/links]))

(defui PersonDetailLinks
  static om/IQueryParams
  (params [this]
    {:link (om/get-query Link)})
  static om/IQuery
  (query [this]
    '[{:link/spouses ?link}
      {:link/change-history ?link}
      {:link/ancestry ?link}
      {:link/notes ?link}
      {:link/non-matches ?link}
      {:link/portraits ?link}
      {:link/ordinances ?link}
      {:link/collection ?link}
      {:link/families ?link}
      {:link/portrait ?link}
      {:link/matches ?link}
      {:link/children ?link}
      {:link/descendancy ?link}
      {:link/person ?link}
      {:link/source-descriptions ?link}
      {:link/merge ?link}
      {:link/ordinance-reservations ?link}
      {:link/artifacts ?link}
      {:link/parents ?link}
      ]))

(defui NameFormPart
  static om/IQuery
  (query [this]
    [:part/type
     :part/value]))

(defui NameForm
  static om/IQueryParams
  (params [this]
    )
  static om/IQuery
  (query [this]
    '[:form/fullText
      {:form/parts ?part}]))

(defui PersonDetailName
  static om/IQueryParams
  (params [this]
    {:name-form (om/get-query PersonDetailNameForm)})
  static om/IQuery
  (query [this]
    '[:name/id
      ;; TODO revisit attribution
      :name/attribution
      :name/type
      :name/preferred
      ;; TODO revisit links
      :name/links
      {:name/nameForms ?name-form}]))

(defui PersonDetail
  static fs/IDatascriptSchema
  (schema [this]
    {:person/names {:db.cardinality/many
                    :db/valueType :db.type/ref
                    :db/isComponent true}})
  static om/IQueryParams
  (params [this]
    {:gender (om/get-query Gender)
     :link   (om/get-query Link)
     :links  (om/get-query PersonDetailLinks)})
  static om/IQuery
  (query [this]
    '[:person/id
      :person/sortKey
      :person/living
      {:person/gender ?gender}
      {:person/links ?links}
      ;; TODO revist identifiers
      :person/identifiers
      {:person/names ?name}

      ]))

(defui Person
  static fs/IDatascriptSchema
  (schema [this]
    {:person/persons {:db/cardinality :db.cardinality/many
                      :db/valueType   :db.type/ref}
     :person/gender  {:db/valueType   :db.type/ref
                      :db/cardinality :db.cardinality/one}})
  static om/IQueryParams
  (params [this]
    {:gender        (om/get-query Gender)
     :person-detail (om/get-query PersonDetail)})
  static om/IQuery
  (query [this]
    '[:person/links
      :person/description
      {:person/persons ?person-detail}]))
