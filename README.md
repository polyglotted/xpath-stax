xpath-stax
==========

[![CircleCI](https://circleci.com/gh/polyglotted/xpath-stax.svg?style=shield)](https://circleci.com/gh/polyglotted/xpath-stax)

XPath expression markup built on top of StAX streaming parser

XPath syntax support is very limited on this version and can parse forward only.
 
 The current supported notations are
 
 * /Root/Node/SubNode - Only fully qualified expressions starting from the root
 * /Root/Node[@attribute] - Supports predicate which find an attribute in any path of the node
 * /Root/Node[@attribute='value'] - Supports predicate which find attribute with matching value (String only supported)
 * /Root/Node/* - Registers all the children of the given node
