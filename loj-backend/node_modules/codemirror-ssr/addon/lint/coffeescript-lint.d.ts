import { Linter } from './lint';

declare module "codemirror" {
    namespace lint {
        const coffeescript: Linter<{}>;
    }
}
export default function use(cm: typeof import('codemirror')): void;