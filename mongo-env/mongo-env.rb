require 'rubygems'
require 'sinatra'
require 'json'
require 'mongo'

get '/' do
  host = ENV['VCAP_APP_HOST']
  port = ENV['VCAP_APP_PORT']
  "<h1>Hello from the Cloud! via: #{host}:#{port}</h1>"
end

get '/mongo' do
  res = '<h1>Hello MongoDB!</h1>'
  vcap_svc = ENV['VCAP_SERVICES']
  res << "#{vcap_svc}"
  services = JSON.parse(vcap_svc)
  mongo_services = services["mongodb-1.8"]
  if mongo_services == nil
    res << "<h3>No Mongo 1.8 service bound to app!</h3>"
  else
    mongo_services.each do |mongo_service|
      name = mongo_service['name']
      res << "<h3>#{name}</h3>"
      credentials = mongo_service["credentials"]
      service_host = credentials["host"]
      service_port = credentials["port"]
      service_db = credentials["db"]
      service_username = credentials["username"]
      service_password = credentials["password"] 
      res << "Host: #{service_host}<br/>"
      res << "Port: #{service_port}<br/>"
      res << "Db: #{service_db}<br/>"
      res << "User: #{service_username}<br/>"
      res << "Collections:<br/>"  
      db = Mongo::Connection.new(service_host, service_port).db(service_db)
      unless db.authenticate(service_username, service_password)
        res << "Quch!!! Not authorized!"
      else
        db.collection_names.each { |name| res << "- #{name}<br/>" }
      end
    end
  end
  res
end

get '/env' do
  res = ''
  ENV.each do |k, v|
    res << "#{k}: #{v}<br/>"
  end
  res
end
