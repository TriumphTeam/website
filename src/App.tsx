import React from "react"
import {CssBaseline} from "@material-ui/core"
import {ThemeProvider} from "@material-ui/core/styles"
import {BrowserRouter as Router, Route, Switch} from "react-router-dom"
import Home from "./pages/Home"
import {darkTheme} from "./components/theme/DarkTheme"
import {SWRConfig} from "swr"
import NotFound from "./pages/NotFound"
import Wiki from "./pages/Wiki"
import api from "./components/axios/Api"

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
        <ThemeProvider theme={darkTheme}>
          <CssBaseline/>
          <SWRConfig value={{
            dedupingInterval: 5000,
            fetcher: (url: string) => api.get(url).then(r => r.data),
            onErrorRetry: (error) => {
              if (error.status === 404) return
            },
          }}>
            <ThemedApp/>
          </SWRConfig>
        </ThemeProvider>
      </React.Fragment>
  )
}

export default App
