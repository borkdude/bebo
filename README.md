# bebo

SCI + deno experiment.

## Install from CDN

To preserve fine-grained control per script invocation, you can use the `run.ts` script to invoke a ClojureScript (`.cljs`) file:

```
deno run --allow-net \
         https://cdn.jsdelivr.net/npm/bebo@0.0.6/run.ts \
         https://raw.githubusercontent.com/borkdude/bebo/main/examples/server/example.cljs
```

and install this invocation as a named tool:

```
deno install --name server-example --allow-net \
     https://cdn.jsdelivr.net/npm/bebo@0.0.6/run.ts \
     https://raw.githubusercontent.com/borkdude/bebo/main/examples/server/example.cljs
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


## Build and run

```
$ npx shadow-cljs release bebo
$ deno run --allow-read --allow-net lib/bebo_main.js examples/server/example.cljs
Listening on http://localhost:8080/
```
