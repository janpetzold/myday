addEventListener('fetch', event => {
    event.respondWith(handleRequest(event.request))
  })
  
  async function handleRequest(request) {
    const url = new URL(request.url)
    if (url.pathname.includes('api')) {
      // API calls to Azure function
      const newURL = `https://myday.azurewebsites.net${url.pathname}`
      return fetch(newURL, request)
    } else {
      // All other calls to website
      return fetch("https://janpetzold.github.io/myday/", request)
    }
  }