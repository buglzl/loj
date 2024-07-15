import './show-hint';

declare module "codemirror" {
    interface HintHelpers {
        anyword: HintFunction;
    }

    interface ShowHintOptions {
        word?: RegExp | undefined;
        range?: number | undefined;
    }
}
export default function use(cm: typeof import('codemirror')): void;