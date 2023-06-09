resource "oci_apigateway_deployment" "patient" {
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.api_gateway.id
  path_prefix    = "/v1/patients"

  display_name = "Patient"

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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/$${request.path[patientId]}"
      }
      methods = ["GET"]
      path    = "/{patientId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider", "/patient", "/service"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/$${request.path[patientId]}"
      }
      methods = ["PUT"]
      path    = "/{patientId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/service", "/patient"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/$${request.path[patientId]}/actions/authorizeDevice"
      }
      methods = ["POST"]
      path    = "/{patientId}/actions/authorizeDevice"
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
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients"
      }
      methods = ["POST"]
      path    = "/"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/service"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/username/$${request.path[username]}"
      }
      methods = ["GET"]
      path    = "/username/{username}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider", "/patient"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/$${request.path[patientId]}"
      }
      methods = ["DELETE"]
      path    = "/{patientId}"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/service"]
        }
      }
    }

    routes {
      backend {
        type = local.route_backend_type
        url  = "${local.ingress_protocol}://${local.ingress_ip}/v1/patients/actions/search"
      }
      methods = ["GET"]
      path    = "/actions/search"
      request_policies {
        authorization {
          type          = local.authorization_type_any_of
          allowed_scope = ["/provider"]
        }
      }
    }

  }
}
