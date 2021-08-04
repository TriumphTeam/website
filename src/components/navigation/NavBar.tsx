import React from "react"
import {createStyles, makeStyles, withStyles} from "@material-ui/core/styles"
import AppBar from "@material-ui/core/AppBar"
import Toolbar from "@material-ui/core/Toolbar"
import Button from "@material-ui/core/Button"
import IconButton from "@material-ui/core/IconButton"

// TODO RESPONSIVE
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
          <IconNavButton edge="start" color="inherit" className={classes.discord}>
            <i className="fab fa-discord"/>
          </IconNavButton>
        </Toolbar>
      </AppBar>
  )
}

const useStyles = makeStyles((theme) =>
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

