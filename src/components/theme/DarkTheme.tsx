import {createMuiTheme} from "@material-ui/core"
import {green} from "@material-ui/core/colors"

export const darkTheme = createMuiTheme({
  palette: {
    type: "dark",
    background: {
      default: "#1A1A1A",
      paper: "#1A1A1A",
    },
    primary: {
      main: "#1A1A1A",
    },
    secondary: {
      main: green[500],
    },
    text: {
      primary: "#96a2b4",
    },
  },
  typography: {
    fontFamily: `"Roboto", sans-serif`,
  },
})