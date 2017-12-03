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

Per la documentazione ufficiale e in particolare per il body della risposta clicca [qui](https://openweathermap.org/current).


### 2. 5 day / 3 hours forecast data
**Richiesta HTTP**
`GET http://api.openweathermap.org/data/2.5/forecast`

| Parametro | Descrizione |
| --------- | ----------- |
| **id** | id della città |
| **units** | "metric" o "imperial" |
| **appid** | api key fornita al momento della registrazione |

Per la documentazione ufficiale e in particolare per il body della risposta clicca [qui](https://openweathermap.org/forecast5).

