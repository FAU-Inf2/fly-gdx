@console = {
  log: (o...) ->
    java.lang.System.out.println(o.join("  "))
}
