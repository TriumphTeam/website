import React, {useEffect, useState} from "react"
import {createStyles, Theme, makeStyles, withStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import Collapse from "@material-ui/core/Collapse"
import ListItem from "@material-ui/core/ListItem"
import ListItemText from "@material-ui/core/ListItemText"
import Toolbar from "@material-ui/core/Toolbar"
import {BarText} from "./components/BarText"
import api from "../axios/Api"
import BarDropdown from "./components/BarDropdown"
import {Entry} from "../axios/Types"

interface SideBarProp {
  entries: Entry[]
}

const drawerWidth = 350

export const SideBar: React.FC<SideBarProp> = ({entries}) => {
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
            {
              entries.map(entry => {
                if (entry.type === "MENU") {
                  const main = entry.main
                  if (main.type === "LINK") {
                    return (<BarDropdown text={main.literal} child={entry.children} defaultOpen={false}/>)
                  }
                }

                if (entry.type === "LINK") {
                  console.log(entry.destination)
                  return (
                      <ListItem button key={entry["literal"]}>
                        <ListItemText primary={entry["literal"]}/>
                      </ListItem>
                  )
                }

                if (entry.type === "HEADER") {
                  return (<BarText text={entry.literal}/>)
                }

                return (<></>)
              })
            }
          </List>
          <Divider/>
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

export default SideBar