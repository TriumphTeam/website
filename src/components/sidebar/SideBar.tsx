import React, {useEffect, useState} from "react"
import {alpha, createStyles, Theme, makeStyles, withStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import InputBase from "@material-ui/core/InputBase"
import Toolbar from "@material-ui/core/Toolbar"
import {BarText} from "./components/BarText"
import api from "../axios/Api"
import BarDropdown from "./components/BarDropdown"
import {Entry} from "../axios/Types"
import BarLink from "./components/BarLink"

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
          <div className={classes.search}>
            <div className={classes.searchIcon}><i className="fas fa-search"/></div>
            <InputBase
                placeholder="Search…"
                classes={{
                  root: classes.inputRoot,
                  input: classes.inputInput,
                }}
                inputProps={{"aria-label": "search"}}
            />
          </div>
          <List>
            {
              entries.map(entry => {
                if (entry.type === "MENU") {
                  const main = entry.main
                  if (main.type === "LINK") {

                    entry.children.map(child => {
                      if (child.type === "LINK") {
                        return <BarLink itemClass={classes.itemClass} textClass={classes.textLinkClass} text={child.literal}/>
                      } else {
                        return <></>
                      }
                    })

                  }
                }

                if (entry.type === "LINK") {
                  console.log(entry.destination)
                  return <BarLink itemClass={classes.itemClass} textClass={classes.textLinkClass} text={entry.literal}/>
                }

                if (entry.type === "HEADER") {
                  return <BarText text={entry.literal}/>
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
        width: "90%",
        marginLeft: "auto",
        marginRight: "auto",
        padding: "20px 0"
      },
      content: {
        flexGrow: 1,
        padding: theme.spacing(3),
      },
      nested: {
        paddingLeft: theme.spacing(4),
      },
      itemClass: {
        margin: 0,
        padding: 0,
      },
      textLinkClass: {
        //padding: "7px 24px 7px 16px",
        //borderWidth: "1px 0px 1px 1px",
        borderYopStyle: "solid",
        borderBottomStyle: "solid",
        borderLeftStyle: "solid",
        borderTopColor: "transparent",
        borderBottomColor: "transparent",
        borderLeftColor: "transparent",
        borderImage: "initial",
        cursor: "pointer",
        display: "flex",
        position: "relative",
        alignItems: "center",
        borderRightStyle: "initial",
        fontSize: ""
      },
      search: {
        position: "relative",
        borderRadius: theme.shape.borderRadius,
        backgroundColor: alpha(theme.palette.common.white, 0.025),
        "&:hover": {
          backgroundColor: alpha(theme.palette.common.white, 0.05),
        },
        marginRight: "auto",
        marginLeft: "auto",
        marginBottom: "10px",
        width: "80%",
      },
      searchIcon: {
        padding: theme.spacing(0, 2),
        height: "100%",
        position: "absolute",
        pointerEvents: "none",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      },
      inputRoot: {
        color: "inherit",
      },
      inputInput: {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
        transition: theme.transitions.create("width"),
        width: "100%",
        [theme.breakpoints.up("md")]: {
          width: "20ch",
        },
      },
    }),
)

export default SideBar