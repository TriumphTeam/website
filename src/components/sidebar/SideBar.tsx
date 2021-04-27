import React from "react"
import {createStyles, Theme, makeStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import Collapse from "@material-ui/core/Collapse"
import ListItem from "@material-ui/core/ListItem"
import ListItemIcon from "@material-ui/core/ListItemIcon"
import ListItemText from "@material-ui/core/ListItemText"
import Toolbar from "@material-ui/core/Toolbar"

const drawerWidth = 240

export default function SideBar() {
  const classes = useStyles()

  const [open, setOpen] = React.useState(false)

  const handleClick = () => {
    setOpen(!open)
  }

  return (
      <Drawer
          className={classes.drawer}
          variant="permanent"
          classes={{
            paper: classes.drawerPaper,
          }}
      >
        <Toolbar className={classes.toolBar}/>
        <div className={classes.drawerContainer}>
          <List>
            {["item", "item", "item", "item"].map((text, index) => (
                <>
                  <ListItem button onClick={handleClick}>
                    <ListItemText primary={text}/>
                    {open ? (<i className="fas fa-angle-up"/>) : (<i className="fas fa-angle-right"/>)}
                  </ListItem>
                  <Collapse in={open} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                      <ListItem button className={classes.nested}>
                        <ListItemText primary="Starred"/>
                      </ListItem>
                    </List>
                  </Collapse>
                </>
            ))}
          </List>
          <Divider/>
          <List>
            {["item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item"].map((text, index) => (
                <ListItem button key={text}>
                  <ListItemText primary={text}/>
                </ListItem>
            ))}
          </List>
        </div>
      </Drawer>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      root: {
        display: "flex",
      },
      toolBar: {
        marginBottom: "10px",
      },
      drawer: {
        width: drawerWidth,
        flexShrink: 0,
      },
      drawerPaper: {
        width: drawerWidth,
      },
      drawerContainer: {
        overflow: "auto",
        "&::-webkit-scrollbar": {
          width: 7,
        },
        "&::-webkit-scrollbar-track": {
          boxShadow: `inset 0 0 6px rgba(0, 0, 0, 0.3)`,
        },
        "&::-webkit-scrollbar-thumb": {
          backgroundColor: "#3C3E41",
          borderRadius: "25px",
        },
      },
      content: {
        flexGrow: 1,
        padding: theme.spacing(3),
      },
      nested: {
        paddingLeft: theme.spacing(4),
      },
    }),
)
