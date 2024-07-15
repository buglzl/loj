import { Linter } from './lint';

declare module "codemirror" {
    namespace lint {
        const css: Linter<any>;
    }
}
export default function use(cm: typeof import('codemirror')): void;