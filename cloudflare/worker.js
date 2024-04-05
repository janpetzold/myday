addEventListener('fetch', event => {
  event.respondWith(handleRequest(event.request))
})

async function handleRequest(request) {
  const url = new URL(request.url)
  if (url.pathname.includes('api')) {
    // Route API calls to Azure function
    const newURL = `https://myday.azurewebsites.net${url.pathname}`
    return fetch(newURL, request)
  } else if(url.pathname.includes(".css")) {
    // Unpretty special rule for Github Pages CSS
    const newURL = `https://janpetzold.github.io/${url.pathname}`
    return fetch(newURL, request)
  } else {
    // Route to website in case the URL is entered
    const newURL = `https://janpetzold.github.io/myday${url.pathname}`
    return fetch(newURL, request)
  }
}