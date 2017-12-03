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
| **appid** | api key fornita al momento della registrazione |

Documentazione ufficiale e body della risposta [qui](https://openweathermap.org/current).


### 2. 5 day / 3 hours forecast data
**Richiesta HTTP**
`GET http://api.openweathermap.org/data/2.5/forecast`

| Parametro | Descrizione |
| --------- | ----------- |
| **id** | id della città |
| **units** | "metric" o "imperial" |
| **appid** | api key fornita al momento della registrazione |

Documentazione ufficiale e body della risposta [qui](https://openweathermap.org/forecast5).


## Facebook
```javascript
const pageId = '1839290216363075'; // identificativo della pagina 'Meteoretidicalcolatori1718'
```
È necessario l'access token.

### 1. Reading Page Albums
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/pageId/albums`

Scope richiesto: pages_show_list

Documentazione ufficiale e body della risposta [qui](https://developers.facebook.com/docs/graph-api/reference/page/albums).

### 2. Reading Page Photos
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/albumId/photos`

Documentazione ufficiale e body della risposta [qui](https://developers.facebook.com/docs/graph-api/reference/page/photos/).

### 3. Reading Photo
**Richiesta HTTP**
`GET https://graph.facebook.com/v2.11/photoId`

| Parametro | Descrizione |
| --------- | ----------- |
| **fields** | lista dei campi da restituire: 'images' per le rappresentazioni della foto |

Documentazione ufficiale [qui](https://developers.facebook.com/docs/graph-api/reference/photo/).
