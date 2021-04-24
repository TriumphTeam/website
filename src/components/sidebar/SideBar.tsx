import React from "react"
import {createStyles, Theme, makeStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import ListItem from "@material-ui/core/ListItem"
import ListItemIcon from "@material-ui/core/ListItemIcon"
import ListItemText from "@material-ui/core/ListItemText"
import Toolbar from "@material-ui/core/Toolbar"

const drawerWidth = 240

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
      },
      content: {
        flexGrow: 1,
        padding: theme.spacing(3),
      },
    }),
)

export default function SideBar() {
  const classes = useStyles()

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
            {["Inbox", "Starred", "Send email", "Drafts"].map((text, index) => (
                <ListItem button key={text}>
                  <ListItemIcon>ICON</ListItemIcon>
                  <ListItemText primary={text}/>
                </ListItem>
            ))}
          </List>
          <Divider/>
          <List>
            {["All mail", "Trash", "Spam"].map((text, index) => (
                <ListItem button key={text}>
                  <ListItemIcon>ICON</ListItemIcon>
                  <ListItemText primary={text}/>
                </ListItem>
            ))}
          </List>
        </div>
      </Drawer>
  )
}