class @Fly
  constructor: (@level) ->
    @listeners = {}

  on: (event, callback) ->
    @listeners[event] ||= []
    @listeners[event].push(callback)

  trigger: (event, args...) ->
    @listeners[event] ||= []
    @listeners[event].forEach (listener) ->
      listener(args...)