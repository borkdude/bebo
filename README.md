# bebo

Deno is a modern runtime for JavaScript and TypeScript. Go to the [deno](https://deno.land/) website to learn more.

This is a tool to run Clojure scripts on deno using
[SCI](https://github.com/babashka/sci).

## Install from CDN

To preserve fine-grained control per script invocation, you can use the `run.ts` script to invoke a ClojureScript (`.cljs`) file:

```
deno run --allow-net \
     https://cdn.jsdelivr.net/npm/bebo@0.0.6/run.ts \
     https://raw.githubusercontent.com/borkdude/bebo/v0.0.6/examples/server/example.cljs
```

and install this invocation as a named tool:

```
deno install --name server-example --allow-net \
     https://cdn.jsdelivr.net/npm/bebo@0.0.6/run.ts \
     https://raw.githubusercontent.com/borkdude/bebo/v0.0.6/examples/server/example.cljs
```

To install `bebo` as a script runner with full access:

```
$ deno install --allow-all --name bebo https://cdn.jsdelivr.net/npm/bebo@0.0.6/lib/bebo_main.js
```

Then run `bebo` on a local or remote `.cljs` file:

```
$ bebo run https://raw.githubusercontent.com/borkdude/bebo/v0.0.6/examples/server/example.cljs
Listening on http://localhost:8080/
```

## Compile

Deno supports a `compile` option which lets you create a standalone
executable. This has the benefit of faster startup time. One disadvantage is
that you have to specify all used dependencies up front. See it as an
optimization when you're done developing your application.

To do this, create a `runner.js`:

``` javascript
import { runScript } from 'https://cdn.jsdelivr.net/npm/bebo@0.0.6/lib/bebo_core.js'

// Add all modules you are going to use within .cljs scripts. They will be bundled into the executable.
import "https://deno.land/std@0.146.0/http/server.ts"

// The .cljs script to be invoked:
await runScript(Deno.args[0]);
```

Then:
```
$ deno compile --allow-all -o runner runner.js
```

Then:

```
./runner examples/server/example.cljs
```

Notice that the startup time using this pre-compiled binary is much faster!

## Build and run

```
$ npx shadow-cljs release bebo
$ deno run --allow-read --allow-net lib/bebo_main.js examples/server/example.cljs
Listening on http://localhost:8080/
```
