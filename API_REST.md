# API REST

## OpenWeatherMap
```javascript
const id = '3169070' // id of Rome
const appid = 'b011edc116685d89dfdc35517d9ce205' // our API key 
```

### 1. Current weather data

**Richiesta HTTP**
`GET http://api.openweathermap.org/data/2.5/weather`

| Parametro | Descrizione |
| --------- | ----------- |
| **id** | id della città |
| **units** | "metric" o "imperial" |
| **lang** | "it" per i risultati in italiano |
| **appid** | api key fornita al momento della registrazione |

Documentazione ufficiale e body della risposta [qui](https://openweathermap.org/current).


### 2. 5 day / 3 hour forecast data
**Richiesta HTTP**
`GET http://api.openweathermap.org/data/2.5/forecast`

| Parametro | Descrizione |
| --------- | ----------- |
| **id** | id della città |
| **units** | "metric" o "imperial" |
| **lang** | "it" per i risultati in italiano |
| **appid** | api key fornita al momento della registrazione |

Documentazione ufficiale e body della risposta [qui](https://openweathermap.org/forecast5).


## Facebook
### 1. Oauth - Exchange authorization code for access token
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/oauth/access_token`

| Parametro | Descrizione |
| --------- | ----------- |
| **client_id** | ottenuto dall'App Dashboard |
| **redirect_uri** | scelto per il progetto nell'App Dashboard |
| **client_secret** | ottenuto dall'App Dashboard |
| **code** | ottenuto a seguito dell'autorizzazione |

Documentazione ufficiale [qui](https://developers.facebook.com/docs/facebook-login/access-tokens#usertokens).


Le successive richieste necessitano dell'access token.

### 2. Reading Page Albums
```javascript
const pageId = '1839290216363075'; // pageId of 'Meteoretidicalcolatori1718'
```

**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/pageId/albums`

Scope richiesti: pages_show_list

Documentazione ufficiale e body della risposta [qui](https://developers.facebook.com/docs/graph-api/reference/page/albums).

### 3. Reading Page Photos
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/albumId/photos`

Documentazione ufficiale e body della risposta [qui](https://developers.facebook.com/docs/graph-api/reference/page/photos/).

### 4. Reading Photo
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/photoId`

| Parametro | Descrizione |
| --------- | ----------- |
| **fields** | lista dei campi da restituire: 'images' per le rappresentazioni della foto |

Documentazione ufficiale [qui](https://developers.facebook.com/docs/graph-api/reference/photo/).

### 5. Post on your Facebook profile
**Richiesta HTTP**
`POST https://graph.facebook.com/v2.11/me/feed`

Scope richiesti: user_posts, publish_actions, manage_pages

| Parametro | Descrizione |
| --------- | ----------- |
| **message** | messaggio da pubblicare |

Documentazione ufficiale [qui](https://developers.facebook.com/docs/graph-api/reference/v2.11/post#publishing)
