import { Linter } from './lint';

declare module "codemirror" {
    namespace lint {
        const javascript: Linter<any>;
    }
}
export default function use(cm: typeof import('codemirror')): void;