import './show-hint';

declare module "codemirror" {
    interface HintHelpers {
        javascript: HintFunction;
        coffeescript: HintFunction;
    }
}
export default function use(cm: typeof import('codemirror')): void;