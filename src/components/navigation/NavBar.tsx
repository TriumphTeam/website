import React from "react"
import {createStyles, DefaultTheme, makeStyles, withStyles} from "@mui/styles"
import {Theme} from "@mui/material/styles"
import AppBar from "@mui/material/AppBar"
import Toolbar from "@mui/material/Toolbar"
import Button from "@mui/material/Button"
import IconButton from "@mui/material/IconButton"

// TODO RESPONSIVE
export default function NavBar() {
  const classes = useStyles()

  return (
    <AppBar position="fixed" className={classes.nav}>
      <Toolbar>
        <IconButton
          edge="start"
          className={classes.menuButton}
          color="inherit"
          aria-label="menu"
          size="large">
          LOGO
        </IconButton>

        {/* Makes content go to the right */}
        <span className={classes.title}/>

        <NavButton color="inherit">HOME</NavButton>
        <NavButton color="inherit">PLUGINS</NavButton>
        <NavButton color="inherit">LIBRARIES</NavButton>
        <IconNavButton edge="start" color="inherit" className={classes.discord}>
          <i className="fab fa-discord"/>
        </IconNavButton>
      </Toolbar>
    </AppBar>
  );
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      nav: {
        padding: "5px",
        zIndex: theme.zIndex.drawer + 1,
        background: theme.palette.background.default,
      },
      menuButton: {
        marginRight: theme.spacing(2),
      },
      title: {
        flexGrow: 1,
      },
      discord: {
        marginLeft: "5px",
      },
    }),
)

const NavButton = withStyles(theme => ({
  root: {
    fontSize: "1.1em",
    transition: "color .25s",
    "&:hover": {
      background: "none",
      color: theme.palette.primary.main,
    },
  },
}))(Button)

const IconNavButton = withStyles(theme => ({
  root: {
    fontSize: "1.6em",
    transition: "color .1s",
    "&:hover": {
      background: "none",
      color: theme.palette.primary.main,
    },
  },
}))(IconButton)

