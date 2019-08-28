# WSO2 Micro Integrator Command Line tool

You can view and manage the Micro Integrator instances using the “mi” command line tool. Some of the usages of the command line tool include,
1. Get a list of deployment runtime artifacts.
2. Inspect details of each runtime artifact such as a proxy service or an API.
3. Get the invocation endpoint of an artifact.

### Running

1. Add the MI CLI bin folder to PATH in UNIX-based Operating System (Linux, Solaris, and Mac OS X)

    `$ export PATH=/path/to/mi/cli/directory/bin:$PATH`

2. Then execute,

    `$ mi`

Execute mi --help for further instructions.

### Add Command Autocompletion (Only for UNIX-based Operating System)

    `$ source /path/to/mi/cli/directory/mi_bash_completion.sh`

### Configuration

##### Enabling the Management API

By default the Management API is disabled. To use the Management API you must use the system property `-DenableManagementApi` when starting the micro integrator

**NOTE: These APIs are not protected using an authorization mechanism. Therefore take extra measures to secure this port if you are enabling this in production.**

##### Changing Management API Address and Port

To configure the address and the port of the Management Api in the CLI use the [**remote**](#remote) command. If no configuration is done, the address and the port will have the default values

NOTE: The default hostname is localhost and the port is 9164.

### Usage
```bash
     mi [command]
```

#### Global Flags
```bash
    --verbose
        Enable verbose logs (Provides more information on execution)
    --help, -h
        Display information and example usage of a command
```
#### Commands
   * ##### remote
```bash
        Usage:
            mi remote [command] [arguments]
                       
        Available Commands:
            add [nick-name] [host] [port]        Add a Micro Integrator
            remove [nick-name]                   Remove a Micro Integrator
            update [nick-name] [host] [port]     Update a Micro Integrator
            select [nick-name]                   Select a Micro Integrator on which commands are executed
            show                                 Show available Micro Integrators

        Examples:
            # To add a Micro Integrator
            mi remote add TestServer 192.168.1.15 9164
            
            # To remove a Micro Integrator
            mi remote remove TestServer
            
            # To update a Micro Integrator
            mi remote update TestServer 192.168.1.17 9164
            
            # To select a Micro Integrator
            mi remote select TestServer
            
            # To show available Micro Integrators
            mi remote show
```
   * ##### log-level
```bash
        Usage:
            mi log-level [command] [arguments]

        Available Commands:
            show [logger-name]                   Show information about a logger
            update [logger-name] [log-level]     Update the log level of a logger

        Examples:
            # Show information about a logger
            mi log-level show org.apache.coyote

            # Update the log level of a logger
            mi log-level update org.apache.coyote DEBUG
```
   * ##### api
```bash
        Usage:
            mi api [command] [argument]

        Available Commands:
            show [api-name]                      Get information about one or more Apis

        Examples:
            # To List all the apis
            mi api show

            # To get details about a specific api
            mi api show sampleApi
```
   * ##### compositeapp
```bash
        Usage:
            mi compositeapp [command] [argument]

        Available Commands:
            show [app-name]                      Get information about one or more Composite apps

        Examples:
            # To List all the composite apps
            mi compositeapp show

            # To get details about a specific composite app
            mi compositeapp show sampleApp
```
   * ##### endpoint
```bash
        Usage:
            mi endpoint [command] [argument]

        Available Commands:
            show [endpoint-name]                 Get information about one or more Endpoints

        Examples:
            # To List all the endpoints
            mi endpoint show

            # To get details about a specific endpoint
            mi endpoint show sampleEndpoint
```
   * ##### inboundendpoint
```bash
        Usage:
            mi inboundendpoint [command] [argument]

        Available Commands:
            show [inboundendpoint-name]          Get information about one or more Inbounds

        Examples:
            # To List all the inbound endpoints
            mi inboundendpoint show

            # To get details about a specific inbound endpoint
            mi inboundendpoint show sampleEndpoint
```
   * ##### proxyservice
```bash
        Usage:
            mi proxyservice [command] [argument]

        Available Commands:
            show [proxyservice-name]             Get information about one or more Proxies

        Examples:
            # To List all the proxy services
            mi proxyservice show

            # To get details about a specific proxy service
            mi proxyservice show sampleProxy
```
   * ##### sequence
```bash
        Usage:
            mi sequence [command] [argument]

        Available Commands:
            show [sequence-name]                 Get information about one or more Sequences

        Examples:
            # To List all the sequences
            mi sequence show

            # To get details about a specific sequence
            mi sequence show sampleProxy
```
   * ##### task
```bash
        Usage:
            mi task [command] [argument]

        Available Commands:
            show [task-name]                     Get information about one or more Tasks

        Examples:
            # To List all the tasks
            mi task show

            # To get details about a specific task
            mi task show sampleProxy
```
   * ##### dataservice
```bash
        Usage:
            mi dataservice [command] [argument]

        Available Commands:
            show [data-service-name]             Get information about one or more Dataservices

        Examples:
            # To List all the dataservices
            mi dataservice show

            # To get details about a specific task
            mi dataservice show SampleDataService
```
* ##### version
```bash
        mi version 
```