import './show-hint';

declare module "codemirror" {
    interface HintHelpers {
        xml: HintFunction;
    }

    interface ShowHintOptions {
        schemaInfo?: any;
        quoteChar?: string | undefined;
        matchInMiddle?: boolean | undefined;
    }
}
export default function use(cm: typeof import('codemirror')): void;