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
}))

export default function NavBar() {
  const classes = useStyles()

  return (
      <AppBar position="fixed" className={classes.nav}>
        <Toolbar>
          <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">
            LOGO
          </IconButton>

          {/* Makes content go to the right */}
          <span className={classes.title}/>

          <NavButton color="inherit">HOME</NavButton>
          <NavButton color="inherit">PLUGINS</NavButton>
          <NavButton color="inherit">LIBRARIES</NavButton>
        </Toolbar>
      </AppBar>
  )

}

const NavButton = withStyles(theme => ({
  root: {
    
  }
}))(Button)