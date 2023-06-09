resource "oci_apigateway_deployment" "provider" {
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.api_gateway.id
  path_prefix    = "/v1/providers"

  display_name = "Provider"

  specification {

    request_policies {
      authentication {
        type              = local.authentication_type
        token_header      = local.token_header
        token_auth_scheme = local.token_auth_scheme
        issuers           = ["${local.issuer}"]
        audiences         = ["https://${oci_apigateway_gateway.api_gateway.hostname}"]
        public_keys {
          type = local.public_keys_type
          keys {
            alg     = local.idcs_public_key["alg"]
            e       = local.idcs_public_key["e"]
            format  = "JSON_WEB_KEY"
            key_ops = local.idcs_public_key["key_ops"]
            kid     = local.idcs_public_key["kid"]
            kty     = local.idcs_public_key["kty"]
            n       = local.idcs_public_key["n"]
            use     = "sig"
          }
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}"
      }
      methods = ["GET"]
      path    = "/{providerId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient", "/provider", "/service"]
        }
      }
    }


    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}"
      }
      methods = ["DELETE"]
      path    = "/{providerId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/admin"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/actions/search"
      }
      methods = ["GET"]
      path    = "/actions/search"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/schedules"
      }
      methods = ["POST"]
      path    = "/{providerId}/schedules"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/admin", "/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/slots"
      }
      methods = ["GET"]
      path    = "/{providerId}/slots"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient", "/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/actions/uploadImage"
      }
      methods = ["POST"]
      path    = "/{providerId}/actions/uploadImage"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/admin", "/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers"
      }
      methods = ["POST"]
      path    = "/"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/admin"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/schedules/$${request.path[scheduleId]}"
      }
      methods = ["DELETE"]
      path    = "/{providerId}/schedules/{scheduleId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/admin", "/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/schedules/$${request.path[scheduleId]}"
      }
      methods = ["GET"]
      path    = "/{providerId}/schedules/{scheduleId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/username/$${request.path[username]}"
      }
      methods = ["GET"]
      path    = "/username/{username}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient", "/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/schedules"
      }
      methods = ["GET"]
      path    = "/{providerId}/schedules"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/feedbacks"
      }
      methods = ["POST"]
      path    = "/{providerId}/feedbacks"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/providers/$${request.path[providerId]}/feedbacks"
      }
      methods = ["GET"]
      path    = "/{providerId}/feedbacks"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/patient", "/provider"]
        }
      }
    }
  }
}