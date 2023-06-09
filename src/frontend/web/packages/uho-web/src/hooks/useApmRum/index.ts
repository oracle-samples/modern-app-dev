/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useEffect, useState } from 'preact/hooks';
import { frontendApi } from 'api';

declare global {
  interface Window {
    apmrum: {
      serviceName: string;
      webApplication: string;
      ociDataUploadEndpoint: string;
      OracleAPMPublicDataKey: string;
    };
  }
}

/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
export function useApmRum() {
  const [appended, setAppended] = useState(false);

  useEffect(() => {
    const scriptElements: HTMLScriptElement[] = [];
    frontendApi.getApmInformation().then((apm) => {
      window.apmrum = {
        OracleAPMPublicDataKey: apm.publicDataKey,
        ociDataUploadEndpoint: apm.ociDataUploadEndpoint,
        serviceName: apm.serviceName,
        webApplication: apm.webApplication
      };

      const script = document.createElement('script');
      script.src = `${apm.ociDataUploadEndpoint}/static/jslib/apmrum.min.js`;
      script.async = true;
      script.crossOrigin = 'anonymous';
      scriptElements.push(document.body.appendChild(script));
      setAppended(true);
    });

    return () => {
      scriptElements.forEach((script) => {
        if (document.body.contains(script)) {
          document.body.removeChild(script);
        }
      });
    };
  }, []);
  return { appended };
}
