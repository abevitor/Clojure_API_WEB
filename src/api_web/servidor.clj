(ns api-web.servidor
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test])
  (:gen-class))

  (def store (atom{}))

  (defn lista-tarefas [request]
  {:status 200 :body @store})

  (defn criar-tarefa-mapa [ uuid nome status]
  { :id uuid :nome nome :status status})

  (defn criar-tarefa [request]
  (let [uuid (java.util.UUID/randomUUID)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (criar-tarefa-mapa uuid nome status)]
      (swap! store assoc uuid tarefa)
      {:status 200
       :body (str "Tarefa criada com sucesso!\n"
       "ID: " uuid "\n"
       "Conteúdo: " tarefa)}))

;; ---------------------------------------------------------
;; HANDLER — função que responde ao endpoint /hello/ criar-tarefa
;; ---------------------------------------------------------

(defn funcao-hello [request]
  {:status 200
   :body (str "Olá, "
              (get-in request [:query-params :name] "visitante")
              "! Seja bem-vindo à API Pedestal!")})

;; ---------------------------------------------------------
;; ROTAS — definem os endpoints da aplicação
;; ---------------------------------------------------------
(def routes
  (route/expand-routes
    #{["/hello" :get funcao-hello :route-name :hello]
    ["/tarefa" :post criar-tarefa :route-name :criar-tarefa]
      ["/tarefa" :get lista-tarefa :route-name :lista-tarefa]}))

;; ---------------------------------------------------------
;; SERVICE MAP — configuração básica do servidor
;; ---------------------------------------------------------
(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 9999
   ::http/join? false})

;; ---------------------------------------------------------
;; TESTE LOCAL (SEM ABRIR SERVIDOR)
;; ---------------------------------------------------------
;; Este trecho cria o serviço na memória e permite simular requests
;; sem precisar abrir uma porta nem subir o Jetty.
(def server
  (atom (http/create-server service-map)))

;; Simula uma requisição GET à rota /hello
(defn testar-hello []
  (test/response-for (::http/service-fn @server)
                     :get "/hello?name=Vitor"))

;; ---------------------------------------------------------
;; FUNÇÃO MAIN (caso queira rodar de verdade)
;; ---------------------------------------------------------
(defn -main [& _]
  (println "🚀 Servidor iniciado em http://localhost:9999 ...")
  (-> service-map
      http/create-server
      http/start))
