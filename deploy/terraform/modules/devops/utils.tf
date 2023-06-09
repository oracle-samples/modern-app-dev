resource "random_string" "repository_id" {
  length      = 8
  lower       = true
  numeric     = true
  special     = false
  upper       = false
  min_lower   = 2
  min_numeric = 2
}