import React from "react"
import {makeStyles, withStyles} from "@material-ui/core/styles"
import AppBar from "@material-ui/core/AppBar"
import Toolbar from "@material-ui/core/Toolbar"
import Typography from "@material-ui/core/Typography"
import Button from "@material-ui/core/Button"
import IconButton from "@material-ui/core/IconButton"
//import MenuIcon from "@material-ui/icons/Menu"

const useStyles = makeStyles((theme) => ({
  nav: {
    padding: "5px",
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
}))

export default function NavBar() {
  const classes = useStyles()

  return (
      <AppBar position="static" className={classes.nav}>
        <Toolbar>
          <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">
            LOGO
          </IconButton>

          {/* Makes content go to the right */}
          <span className={classes.title}/>

          <NavButton color="inherit">HOME</NavButton>
          <NavButton color="inherit">PLUGINS</NavButton>
          <NavButton color="inherit">LIBRARIES</NavButton>
          <IconNavButton edge="start" color="inherit" className={classes.discord}><i
              className="fab fa-discord"/></IconNavButton>
        </Toolbar>
      </AppBar>
  )

}

const NavButton = withStyles(theme => ({
  root: {
    fontSize: "1.1em",
    color: "#ecf0f1",
    transition: "color .25s",
    "&:hover": {
      background: "none",
      color: "#2980b9",
    },
  },
}))(Button)

const IconNavButton = withStyles(theme => ({
  root: {
    fontSize: "1.6em",
    color: "#ecf0f1",
    transition: "color .25s",
    "&:hover": {
      background: "none",
      color: "#2980b9",
    },
  },
}))(IconButton)