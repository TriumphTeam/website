package tasks

val RUN_POSTGRES = """
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="RunPostgress" type="docker-deploy" factoryName="docker-image" server-name="Docker">
    <deployment type="docker-image">
      <settings>
        <option name="imageTag" value="postgres:15.17" />
        <option name="containerName" value="postgres-website" />
        <option name="envVars">
          <list>
            <DockerEnvVarImpl>
              <option name="name" value="POSTGRES_DB" />
              <option name="value" value="triumph" />
            </DockerEnvVarImpl>
            <DockerEnvVarImpl>
              <option name="name" value="POSTGRES_USER" />
              <option name="value" value="triumph" />
            </DockerEnvVarImpl>
            <DockerEnvVarImpl>
              <option name="name" value="POSTGRES_PASSWORD" />
              <option name="value" value="triumph" />
            </DockerEnvVarImpl>
          </list>
        </option>
        <option name="portBindings">
          <list>
            <DockerPortBindingImpl>
              <option name="containerPort" value="5432" />
              <option name="hostPort" value="5432" />
            </DockerPortBindingImpl>
          </list>
        </option>
      </settings>
    </deployment>
    <method v="2" />
  </configuration>
</component>
""".trimIndent()

val RUN_BACKEND = """
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="RunBackend" type="GradleRunConfiguration" factoryName="Gradle">
    <ExternalSystemSettings>
      <option name="executionName" />
      <option name="externalProjectPath" value="${'$'}PROJECT_DIR$/backend" />
      <option name="externalSystemIdString" value="GRADLE" />
      <option name="scriptParameters" value="" />
      <option name="taskDescriptions">
        <list />
      </option>
      <option name="taskNames">
        <list>
          <option value="run" />
        </list>
      </option>
      <option name="vmOptions" />
    </ExternalSystemSettings>
    <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
    <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
    <ExternalSystemDebugDisabled>false</ExternalSystemDebugDisabled>
    <DebugAllEnabled>false</DebugAllEnabled>
    <RunAsTest>false</RunAsTest>
    <GradleProfilingDisabled>false</GradleProfilingDisabled>
    <GradleCoverageDisabled>false</GradleCoverageDisabled>
    <method v="2">
      <option name="RunConfigurationTask" enabled="true" run_configuration_name="RunPosgress" run_configuration_type="docker-deploy" />
    </method>
  </configuration>
</component>
""".trimIndent()

val RUN_DOCS_UPLOAD = """
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="RunDocsUpload" type="GradleRunConfiguration" factoryName="Gradle">
    <ExternalSystemSettings>
      <option name="executionName" />
      <option name="externalProjectPath" value="${'$'}PROJECT_DIR$/docs" />
      <option name="externalSystemIdString" value="GRADLE" />
      <option name="scriptParameters" value="" />
      <option name="taskDescriptions">
        <list />
      </option>
      <option name="taskNames">
        <list>
          <option value="run" />
          <option value="--args=&quot;-i ../data&quot;" />
        </list>
      </option>
      <option name="vmOptions" value="" />
    </ExternalSystemSettings>
    <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
    <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
    <ExternalSystemDebugDisabled>false</ExternalSystemDebugDisabled>
    <DebugAllEnabled>false</DebugAllEnabled>
    <RunAsTest>false</RunAsTest>
    <GradleProfilingDisabled>false</GradleProfilingDisabled>
    <GradleCoverageDisabled>false</GradleCoverageDisabled>
    <method v="2" />
  </configuration>
</component>
""".trimIndent()

val RUN_FRONTEND = """
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="RunFrontend" type="GradleRunConfiguration" factoryName="Gradle">
    <ExternalSystemSettings>
      <option name="executionName" />
      <option name="externalProjectPath" value="${'$'}PROJECT_DIR$/frontend" />
      <option name="externalSystemIdString" value="GRADLE" />
      <option name="scriptParameters" value="--continuous" />
      <option name="taskDescriptions">
        <list />
      </option>
      <option name="taskNames">
        <list>
          <option value="jsViteDev" />
        </list>
      </option>
      <option name="vmOptions" />
    </ExternalSystemSettings>
    <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
    <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
    <ExternalSystemDebugDisabled>false</ExternalSystemDebugDisabled>
    <DebugAllEnabled>false</DebugAllEnabled>
    <RunAsTest>false</RunAsTest>
    <GradleProfilingDisabled>false</GradleProfilingDisabled>
    <GradleCoverageDisabled>false</GradleCoverageDisabled>
    <method v="2" />
  </configuration>
</component>
""".trimIndent()
