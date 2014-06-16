fly.on 'render', ->
  it = fly.level.allGates().iterator()
  while it.hasNext()
    x = it.next()
    x.display.transform.rotate(new com.badlogic.gdx.math.Vector3(0, 0, 1), 0.5);