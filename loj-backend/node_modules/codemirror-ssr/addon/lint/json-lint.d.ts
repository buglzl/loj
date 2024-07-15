import { Linter } from './lint';

declare module "codemirror" {
    namespace lint {
        const json: Linter<{}>;
    }
}
export default function use(cm: typeof import('codemirror')): void;