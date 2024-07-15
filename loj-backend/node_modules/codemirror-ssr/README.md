# codemirror-ssr

SSR compatible CodeMirror 5

## Motivation

[CodeMirror 5](https://github.com/codemirror/CodeMirror) uses some client-only APIs at top level, which makes it not compatible with SSR. A way to solve this problem is to dynamic import `codemirror` module and its addons in client-only lifecycles. It works, but would cause unnecessary bundle split and increase the final loading time.

## Usage

### Before

```js
import codemirror from "codemirror";
import "codemirror/addon/display/placeholder";

const editor = codemirror.fromTextArea(textarea, {
  // options
});
```

### After

```js
import factory from "codemirror-ssr";
import usePlaceholder from "codemirror-ssr/addon/display/placeholder";

// the client-only lifecycle
function mounted() {
  const codemirror = factory();
  usePlaceholder(codemirror); // apply addons manually

  const editor = codemirror.fromTextArea(textarea, {
    // options
  });
}
```

## Built with codemirror-ssr

- [ByteMD](https://github.com/bytedance/bytemd): A hackable Markdown editor component built with Svelte

## License

MIT
