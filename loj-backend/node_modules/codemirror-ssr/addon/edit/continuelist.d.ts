import "codemirror";

declare module "codemirror" {
    interface CommandActions {
        newlineAndIndentContinueMarkdownList(cm: Editor): void | typeof Pass;
    }
}
export default function use(cm: typeof import('codemirror')): void;