import {createTheme, Theme} from "@mui/material/styles"
import {green} from "@mui/material/colors"

declare module "@mui/material/styles" {
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  interface DefaultTheme extends Theme {
  }
}

export const darkTheme = createTheme({
  palette: {
    mode: "dark",
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
