import React from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {Link} from "react-router-dom"

type BarLinkProp = {
  text: string,
  indent: number,
  destination: string,
  active?: boolean,
}

export const BarLink: React.FC<BarLinkProp> = ({text, indent, destination, active}) => {
  const classes = useStyles()

  const Indentation = () => {
    if (indent === 1) {
      return <span className={classes.indent}/>
    }

    return <></>
  }

  const activeClass = (active ? ` ${classes.active}` : "")

  return (
      <Link to={destination} className={classes.link}>
        <Indentation/>
        <div className={`${classes.linkContainer}${activeClass}`}>{text}</div>
      </Link>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      link: {
        width: "80%",
        marginLeft: "auto",
        marginRight: "auto",
        textDecoration: "none",
        display: "box",
      },
      linkContainer: {
        fontSize: "1.15em",
        padding: "5px 0",
        color: "#FFFFFFB3",
        transition: "color .25s",
        "&:hover": {
          background: "none",
          color: theme.palette.primary.main,
        },
      },
      indent: {
        marginRight: "25px",
      },
      active: {
        color: theme.palette.primary.main,
      },
    }),
)

export default BarLink
