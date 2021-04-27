import {createMuiTheme} from "@material-ui/core"
import {green} from "@material-ui/core/colors"

export const darkTheme = createMuiTheme({
  palette: {
    type: "dark",
    background: {
      default: "#1C1E21",
      paper: "#18191A",
    },
    primary: {
      main: "#18191A",
    },
    secondary: {
      main: green[500],
    },
    text: {
      primary: "#96a2b4",
    },
  },
  typography: {
    fontFamily: `"Cabin", sans-serif`,
  },
})