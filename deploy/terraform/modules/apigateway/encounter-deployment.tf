resource "oci_apigateway_deployment" "encounter" {
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.api_gateway.id
  path_prefix    = "/v1/encounters"

  display_name = "Encounter"

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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters/$${request.path[encounterId]}"
      }
      methods = ["GET"]
      path    = "/{encounterId}"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters"
      }
      methods = ["POST"]
      path    = "/"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters/actions/search"
      }
      methods = ["GET"]
      path    = "/actions/search"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters/$${request.path[encounterId]}"
      }
      methods = ["PUT"]
      path    = "/{encounterId}"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters/$${request.path[encounterId]}"
      }
      methods = ["DELETE"]
      path    = "/{encounterId}"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/encounters/codes"
      }
      methods = ["GET"]
      path    = "/codes"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider", "/patient"]
        }
      }
    }
  }
}