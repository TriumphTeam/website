import React from "react"
import {CssBaseline, makeStyles} from "@material-ui/core"
import {ThemeProvider} from "@material-ui/core/styles"
import {BrowserRouter as Router, Route, Switch} from "react-router-dom"
import Home from "./pages/Home"
import {darkTheme} from "./components/theme/DarkTheme"
import NotFound from "./pages/NotFound"

const baseStyle = makeStyles(theme => ({
  body: {
    background: theme.palette.background.default,
  },
}))

function ThemedApp() {
  return (
      <Router>
        <Switch>
          <Route path="/" exact component={Home}/>
          <Route path="/plugin/:name" exact component={Home}/>
          <Route path="/lib/:name" exact component={Home}/>

          <Route path="/plugin/:name/wiki" component={Home}/>
          <Route path="/lib/:name/wiki" component={Home}/>

          <Route component={NotFound} />
        </Switch>
      </Router>
  )
}

function App() {
  return (
      <React.Fragment>
        <ThemeProvider theme={darkTheme}>
          <CssBaseline/>
          <ThemedApp/>
        </ThemeProvider>
      </React.Fragment>
  )
}

export default App
