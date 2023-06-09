resource "oci_waf_web_app_firewall_policy" "web_app_firewall_policy" {
  compartment_id = var.compartment_id
  display_name   = "refapp_waf_policy"

  actions {
    name = "Check Action"
    type = "CHECK"
  }

  actions {
    name = "Allow Action"
    type = "ALLOW"
  }

  actions {
    name = "401 Response Code Action"
    type = "RETURN_HTTP_RESPONSE"
    code = 401

    body {
      type = "STATIC_TEXT"
      text = "{\n\"code\": 401,\n\"message\":\"Unauthorized\"\n}"
    }

    headers {
      name  = "Content-Type"
      value = "application/json"
    }
  }

  actions {
    name = "429 Response Code Action"
    type = "RETURN_HTTP_RESPONSE"
    code = 429

    body {
      type = "STATIC_TEXT"
      text = "{\n\"code\": 429,\n\"message\":\"Too Many Requests\"\n}"
    }

    headers {
      name  = "Content-Type"
      value = "application/json"
    }
  }

  actions {
    name = "403 Response Code Action"
    type = "RETURN_HTTP_RESPONSE"
    code = 403

    body {
      type = "STATIC_TEXT"
      text = "{\n\"code\": 403,\n\"message\":\"Forbidden\"\n}"
    }

    headers {
      name  = "Content-Type"
      value = "application/json"
    }
  }

  request_protection {
    rules {
      type        = "PROTECTION"
      name        = "requestProtectionRule"
      action_name = "403 Response Code Action"
      protection_capabilities {
        key                            = "9420000"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9421000"
          weight = 4
        }
        collaborative_weights {
          key    = "9421400"
          weight = 4
        }
        collaborative_weights {
          key    = "9421600"
          weight = 4
        }
        collaborative_weights {
          key    = "9421700"
          weight = 4
        }
        collaborative_weights {
          key    = "9421900"
          weight = 4
        }
        collaborative_weights {
          key    = "9422200"
          weight = 4
        }
        collaborative_weights {
          key    = "9422300"
          weight = 4
        }
        collaborative_weights {
          key    = "9422400"
          weight = 4
        }
        collaborative_weights {
          key    = "9422500"
          weight = 4
        }
        collaborative_weights {
          key    = "9422700"
          weight = 4
        }
        collaborative_weights {
          key    = "9422800"
          weight = 4
        }
        collaborative_weights {
          key    = "9422900"
          weight = 4
        }
        collaborative_weights {
          key    = "9423200"
          weight = 4
        }
        collaborative_weights {
          key    = "9423500"
          weight = 4
        }
        collaborative_weights {
          key    = "9423600"
          weight = 4
        }
        collaborative_weights {
          key    = "9421100"
          weight = 4
        }
      }
      protection_capabilities {
        key     = "941140"
        version = 2
      }
      protection_capabilities {
        key                            = "9410000"
        version                        = 2
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9411000"
          weight = 4
        }
        collaborative_weights {
          key    = "9411100"
          weight = 4
        }
        collaborative_weights {
          key    = "9411200"
          weight = 4
        }
        collaborative_weights {
          key    = "9411300"
          weight = 4
        }
        collaborative_weights {
          key    = "9411400"
          weight = 4
        }
        collaborative_weights {
          key    = "9411600"
          weight = 4
        }
        collaborative_weights {
          key    = "9411700"
          weight = 4
        }
        collaborative_weights {
          key    = "9411800"
          weight = 4
        }
        collaborative_weights {
          key    = "9411900"
          weight = 4
        }
        collaborative_weights {
          key    = "9412000"
          weight = 4
        }
        collaborative_weights {
          key    = "9412100"
          weight = 4
        }
        collaborative_weights {
          key    = "9412200"
          weight = 4
        }
        collaborative_weights {
          key    = "9412300"
          weight = 4
        }
        collaborative_weights {
          key    = "9412400"
          weight = 4
        }
        collaborative_weights {
          key    = "9412500"
          weight = 4
        }
      }
      protection_capabilities {
        key                            = "9410000"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9411000"
          weight = 4
        }
        collaborative_weights {
          key    = "9411100"
          weight = 4
        }
        collaborative_weights {
          key    = "9411200"
          weight = 4
        }
        collaborative_weights {
          key    = "9411300"
          weight = 4
        }
        collaborative_weights {
          key    = "9411400"
          weight = 4
        }
        collaborative_weights {
          key    = "9411600"
          weight = 4
        }
        collaborative_weights {
          key    = "9411700"
          weight = 4
        }
        collaborative_weights {
          key    = "9411800"
          weight = 4
        }
        collaborative_weights {
          key    = "9411900"
          weight = 4
        }
        collaborative_weights {
          key    = "9412000"
          weight = 4
        }
        collaborative_weights {
          key    = "9412100"
          weight = 4
        }
        collaborative_weights {
          key    = "9412200"
          weight = 4
        }
        collaborative_weights {
          key    = "9412300"
          weight = 4
        }
        collaborative_weights {
          key    = "9412400"
          weight = 4
        }
        collaborative_weights {
          key    = "9412500"
          weight = 4
        }
      }
      protection_capabilities {
        key                            = "9330000"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9331000"
          weight = 4
        }
        collaborative_weights {
          key    = "9331100"
          weight = 4
        }
        collaborative_weights {
          key    = "9331200"
          weight = 4
        }
        collaborative_weights {
          key    = "9331300"
          weight = 4
        }
        collaborative_weights {
          key    = "9331400"
          weight = 4
        }
        collaborative_weights {
          key    = "9331500"
          weight = 4
        }
        collaborative_weights {
          key    = "9331600"
          weight = 4
        }
        collaborative_weights {
          key    = "9331700"
          weight = 4
        }
        collaborative_weights {
          key    = "9331800"
          weight = 4
        }
        collaborative_weights {
          key    = "9331510"
          weight = 4
        }
        collaborative_weights {
          key    = "9331310"
          weight = 4
        }
        collaborative_weights {
          key    = "9331610"
          weight = 4
        }
        collaborative_weights {
          key    = "9331110"
          weight = 4
        }
      }
      protection_capabilities {
        key                            = "9320001"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9321100"
          weight = 4
        }
        collaborative_weights {
          key    = "9321150"
          weight = 4
        }
        collaborative_weights {
          key    = "9321200"
          weight = 4
        }
        collaborative_weights {
          key    = "9321400"
          weight = 4
        }
      }
      protection_capabilities {
        key                            = "9320000"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9321000"
          weight = 4
        }
        collaborative_weights {
          key    = "9321050"
          weight = 4
        }
        collaborative_weights {
          key    = "9321300"
          weight = 4
        }
        collaborative_weights {
          key    = "9321500"
          weight = 4
        }
        collaborative_weights {
          key    = "9321600"
          weight = 4
        }
      }
      protection_capabilities {
        key     = "930120"
        version = 2
      }
      protection_capabilities {
        key                            = "9300000"
        version                        = 1
        collaborative_action_threshold = 10
        collaborative_weights {
          key    = "9301000"
          weight = 2
        }
        collaborative_weights {
          key    = "9301100"
          weight = 4
        }
        collaborative_weights {
          key    = "9301200"
          weight = 4
        }
        collaborative_weights {
          key    = "9301300"
          weight = 4
        }
      }
      protection_capabilities {
        key     = "920390"
        version = 1
      }
      protection_capabilities {
        key     = "920380"
        version = 1
      }
      protection_capabilities {
        key     = "920370"
        version = 1
      }
      protection_capabilities {
        key     = "920320"
        version = 1
      }
      protection_capabilities {
        key     = "920300"
        version = 1
      }
      protection_capabilities {
        key     = "911100"
        version = 1
      }
    }
  }

  request_rate_limiting {
    rules {
      type        = "REQUEST_RATE_LIMITING"
      name        = "requestRateLimitingRule"
      action_name = "429 Response Code Action"
      configurations {
        period_in_seconds = 100
        requests_limit    = 10
      }
    }
  }
}

data "oci_load_balancer_load_balancers" "test_load_balancers" {
  compartment_id = var.compartment_id
  filter {
    name   = "ip_addresses"
    values = [var.ingress_ip]
  }
}

resource "oci_waf_web_app_firewall" "web_app_firewall" {
  backend_type               = "LOAD_BALANCER"
  compartment_id             = var.compartment_id
  load_balancer_id           = data.oci_load_balancer_load_balancers.test_load_balancers.load_balancers.0.id
  web_app_firewall_policy_id = oci_waf_web_app_firewall_policy.web_app_firewall_policy.id
  display_name               = "refapp_waf"
}