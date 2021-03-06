import React from "react"
import {CssBaseline} from "@mui/material"
import {StyledEngineProvider, ThemeProvider} from "@mui/material/styles"
import {BrowserRouter as Router, Route, Switch} from "react-router-dom"
import Home from "./pages/home/Home"
import {darkTheme} from "./components/theme/DarkTheme"
import {SWRConfig} from "swr"
import NotFound from "./pages/NotFound"
import Wiki from "./pages/wiki/Wiki"
import api from "./components/axios/Api"

import "./global.scss"

//import "./fontawesome/fontawesome.scss"

function ThemedApp() {
  return (
      <Router>
        <Switch>
          <Route path="/" exact component={Home}/>
          <Route path="/:type/:project/:page?" component={Wiki}/>

          <Route component={NotFound}/>
        </Switch>
      </Router>
  )
}

function App() {
  return (
      <React.Fragment>
        <StyledEngineProvider injectFirst>
          <ThemeProvider theme={darkTheme}>
            <CssBaseline/>
            <SWRConfig value={{
              dedupingInterval: 15000,
              fetcher: (url: string) => api.get(url).then(r => r.data),
              onErrorRetry: (error) => {
                if (error.status === 404) return
              },
            }}>
              <ThemedApp/>
            </SWRConfig>
          </ThemeProvider>
        </StyledEngineProvider>
      </React.Fragment>
  )
}

export default App
