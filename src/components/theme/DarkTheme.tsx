import {createMuiTheme} from "@material-ui/core";
import {green} from "@material-ui/core/colors"

export const darkTheme = createMuiTheme({
  palette: {
    type: "dark",
    background: {
      default: "#232B3E",
      paper: "#1A202E",
    },
    primary: {
      main: "#B06AB3",
    },
    secondary: {
      main: green[500],
    },
    text: {
      primary: "#96a2b4",
    },
  },
})