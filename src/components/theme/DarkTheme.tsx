import {createTheme} from "@material-ui/core"
import {green} from "@material-ui/core/colors"

export const darkTheme = createTheme({
  palette: {
    type: "dark",
    background: {
      default: "#1A1A1A",
      paper: "#1A1A1A",
    },
    primary: {
      main: "#3498db",
    },
    secondary: {
      // TODO change
      main: green[500],
    },
    text: {
      primary: "#FFFFFF",
      secondary: "#96a2b4",
    },
  },
  typography: {
    fontFamily: `"Roboto", sans-serif`,
  },
})
