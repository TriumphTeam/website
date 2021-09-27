import Particles from "react-particles-js"
import React from "react"

const CircleParticle = () => <Particles
    params={{
      particles: {
        color: {
          value: "#26c885",
        },
        links: {
          enable: false,
        },
        size: {
          value: 50,
          animation: {
            enable: false,
          },
          random: {
            enable: true,
            minimumValue: 5,
          },
        },
        opacity: {
          value: 0.3,
          random: {
            enable: true,
            minimumValue: 0.005,
          },
          animation: {
            enable: false,
          },
        },
        move: {
          speed: 0.1,
        },
        number: {
          density: {
            enable: false,
          },
          value: 15,
        },
      },

    }}
/>

export default CircleParticle
