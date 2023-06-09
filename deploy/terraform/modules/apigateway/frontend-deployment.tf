resource "oci_apigateway_deployment" "home" {
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.api_gateway.id
  path_prefix    = "/home"

  display_name = "Frontend"

  specification {

    routes {
      backend {
        type = "HTTP_BACKEND"
        url  = "${local.ingress_protocol}://${local.ingress_ip}/home"
      }
      methods = ["GET"]
      path    = "/"
    }

    routes {
      backend {
        type = "HTTP_BACKEND"
        url  = "${local.ingress_protocol}://${local.ingress_ip}/home/$${request.path[patient]}"
      }
      methods = ["ANY"]
      path    = "/{patient*}"
    }

    routes {
      backend {
        type = "HTTP_BACKEND"
        url  = "${local.ingress_protocol}://${local.ingress_ip}/home/$${request.path[provider]}"
      }
      methods = ["ANY"]
      path    = "/{provider*}"
    }

    routes {
      backend {
        type = "HTTP_BACKEND"
        url  = "${local.ingress_protocol}://${local.ingress_ip}/home/oauth/logout"
      }
      methods = ["GET"]
      path    = "/oauth/logout"
    }
  }
}

resource "oci_apigateway_deployment" "oauth" {
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.api_gateway.id
  path_prefix    = "/oauth"

  display_name = "Oauth"

  specification {
    logging_policies {
      execution_log {
        is_enabled = "true"
        log_level  = "ERROR"
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/oauth/callback/$${request.path[provider]}"
      }
      methods = ["GET", "POST"]
      path    = "/callback/{provider}"
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/oauth/login/$${request.path[provider]}"
      }
      methods = ["GET", "POST"]
      path    = "/login/{provider}"
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/home/logout"
      }
      methods = ["GET", "POST"]
      path    = "/logout"
    }
  }
}

